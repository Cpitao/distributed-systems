from fastapi import FastAPI, status
from fastapi.responses import Response, HTMLResponse
from starlette.exceptions import HTTPException as StarletteHTTPException
from fastapi.security import HTTPBasic, HTTPBasicCredentials

import cat_api as cat_response


app = FastAPI()


@app.exception_handler(StarletteHTTPException)
async def exception_handler(request, err):
    image_bytes: bytes = cat_response.get_cat(err.status_code)
    if image_bytes is None:
        return Response(status_code=err.status_code,
                        content=f"I tried my best to raise a fancy HTTP {err.status_code} {err.detail},"
                                f" but the cat is gone somewhere")
    return Response(status_code=err.status_code, content=image_bytes, media_type="image/png")


@app.get("/", response_class=HTMLResponse)
async def get_main_page():
    return """
    <html>
        <body>
        <h1>Welcome to the main page</h1>
        </body>
    </html>
    """