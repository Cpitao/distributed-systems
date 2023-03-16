import requests


def get_cat(status: int):
    url = "https://http.cat/"
    try:
        response = requests.get(url + f"{status}")
    except requests.exceptions.ConnectionError:
        return None

    if response.status_code != 200:
        return None
    return response.content


if __name__ == "__main__":
    get_cat(404)