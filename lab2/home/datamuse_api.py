import requests
import re


class DatamuseAPI:

    URL = "http://api.datamuse.com/words?sp="

    def get_similar(self, word: str, top: int = 10):
        regex = re.compile("[^a-zA-Z]")
        word = regex.sub('', word)
        response = requests.get(DatamuseAPI.URL + word)
        if response.status_code != 200:
            return None

        resp = response.json()
        return list(map(lambda x: x[1],
                        sorted(list(map(lambda x: (x["score"], x["word"]), resp)), reverse=True)[:top]))


if __name__ == "__main__":
    api = DatamuseAPI()
    print(api.get_similar("passw0rd123"))