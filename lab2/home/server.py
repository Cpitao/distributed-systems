from fastapi import FastAPI, Form, Request, Depends, status
from fastapi.responses import Response, HTMLResponse
from fastapi.exceptions import RequestValidationError, HTTPException
from starlette.exceptions import HTTPException as StarletteHTTPException
from fastapi.security import HTTPBasic, HTTPBasicCredentials
from pydantic import BaseModel
import secrets
from typing import Union

import cat_api as cat_response
import hibp_api
import hasher
import password_variations
import datamuse_api
import textrazor_api

app = FastAPI()

security = HTTPBasic()


class Password(BaseModel):
    value: str


def verify_user(credentials: HTTPBasicCredentials):
    current_username_bytes = credentials.username.encode('utf-8')
    current_password_bytes = credentials.password.encode('utf-8')
    with open("credentials", "r") as f:
        correct_username, correct_password = f.read().split(sep=':')
        correct_username_bytes = correct_username.encode('utf-8')
        correct_password_bytes = correct_password.encode('utf-8')

    is_correct_username = secrets.compare_digest(current_username_bytes, correct_username_bytes)
    is_correct_password = secrets.compare_digest(current_password_bytes, correct_password_bytes)

    if not (is_correct_username and is_correct_password):
        raise StarletteHTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Basic"}
        )


@app.exception_handler(StarletteHTTPException)
async def http_exception_handler(request: Request, err: StarletteHTTPException):
    if err.status_code == 401:
        return HTMLResponse(status_code=err.status_code, content="""
        <p>You must log in first</p>
        <img src="/error/401" alt="I tried to get you an HTTP cat but it's gone somewhere"></img>
        """,
                            headers={"WWW-Authenticate": "Basic"})

    return HTMLResponse(status_code=err.status_code, content=f"""
        <p>There was an error. Verify requested data and try again.</p>
        <img src="/error/{err.status_code}" alt="I tried to get you an HTTP cat but it's gone somewhere"></img>
        """)


@app.get("/error/{status}")
async def get_error_image(status: int):
    image_bytes: bytes = cat_response.get_cat(status)
    if image_bytes is None:
        return Response(status_code=status,
                        content=f"I tried my best to raise a fancy HTTP {status} error,"
                                f" but my cat is gone somewhere")

    return Response(status_code=status, content=image_bytes, media_type="image/png")


@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request, err):
    return HTMLResponse(status_code=422, content=f"""
        <p>Data you provided is incorrect. Remember to fill all the necessary data.</p>
        <img src="/error/{422}" alt="I tried to get you an HTTP cat but it's gone somewhere"></img>
        """)


@app.get("/", response_class=HTMLResponse)
async def main(credentials: HTTPBasicCredentials = Depends(security)):
    verify_user(credentials)

    return """
    <html>
        <body>
        <h1> Check if password is secure:</h1>
        <form action="/check" method="post">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password"><br>
            <label for="variations">Try N variations of your password:</label>
            <input type="number" id="variations" name="variations" min="0" max="20" value="10"><br>
            <label for="top_similar">Show up to top N similar passwords:</label>
            <input type="number" id="top_similar" name="top_similar" min="0" max="100" value="10"><br>
            <label for="dict_url">Check if your password could be brute-forced with wordlist from given page:</label>
            <input type="text" name="dict_url"><br>
            <input type="submit" value="Check">
        </form>
        <p style="color:red">Warning: the bigger the numbers, the longer it may take to process your request</p>
        </body>
    </html>
    """


@app.post("/check", response_class=HTMLResponse)
async def check_password(password: str = Form(), top_similar: int = Form(),
                         variations: int = Form(), dict_url: str = Form(None),
                         credentials: HTTPBasicCredentials = Depends(security)):

    verify_user(credentials)

    if not password:
        raise StarletteHTTPException(status_code=422)

    similar_passwords_leaked = get_leaked_passwords(password, variations)

    datamuseApi = datamuse_api.DatamuseAPI()
    similar_words = datamuseApi.get_similar(password, max(0, min(top_similar, 100)))

    password_strength = "<p style=\"color:green\">good</p>" if sum(similar_passwords_leaked.values()) == 0 \
        else "<p style=\"color:red\">bad</p>"

    wordlist_html = ""
    if dict_url is not None:
        textrazorApi = textrazor_api.TextrazorAPI()
        textrazorApi.get_keywords(dict_url)
        best_match = textrazorApi.best_matches(password)
        if best_match is not None:
            wordlist_html = f"<p>The closest word from given URL to your password would be {best_match}</p>"
            password_strength = "<p style=\"color:red\">bad</p>"
        else:
            wordlist_html = "<p>The URL you provided is either broken or yielded no keywords</p>"

    if sum(similar_passwords_leaked.values()) > 0:
        html_table = "<h3>Similar passwords leaked</h3>" \
                     "<table><tr><th>Password</th><th>Number of occurrences</th></tr>"
        for p, count in sorted(similar_passwords_leaked.items(), key=lambda x: x[1], reverse=True):
            html_table += f"<tr><td>{p}</td><td>{count}</td></tr>"
        html_table += "</table>"
    else:
        html_table = "This password or it's variations have not been leaked"

    if len(similar_words) > 0:
        similar_words_html = """
        <h3>Your password could be derived from the following words:</h3>
        <ul>
        """

        for word in similar_words:
            similar_words_html += f"<li>{word}</li>\n"
        similar_words_html += "</ul>"
        password_strength = "<p style=\"color:red\">bad</p>"
    else:
        similar_words_html = ""

    return f"""
<html>
    <body>
        <a href="/">Go back</a>
        <h3>Your password is {password_strength}</h3>
        {html_table}
        {similar_words_html}<br>
        {wordlist_html}
    </body>
</html>
"""


def get_leaked_passwords(password, variations):
    passwords_to_check = password_variations.generate_variations(password, max(0, min(variations, 100)))
    password_api = hibp_api.HibpAPI()
    similar_passwords_leaked = {}
    for p in passwords_to_check:
        full_hash = hasher.sha1(p.encode('utf-8')).hexdigest()
        hash_start = hasher.get_first_sha1(p)
        result = password_api.query_api(hash_start)
        for key in result.keys():
            if hash_start.lower() + key.lower() == full_hash.lower():
                similar_passwords_leaked[p] = result[key]
                break
        from time import sleep
        sleep(0.1)  # rate-limit sent requests

    return similar_passwords_leaked
