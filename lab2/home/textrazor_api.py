import requests
import Levenshtein


class TextrazorAPI:

    URL = "http://api.textrazor.com/"

    def __init__(self):
        with open('textrazor.key', 'r') as f:
            self.key = f.read()
        self.headers = {"x-textrazor-key": self.key}
        self.words = set()

    def get_keywords(self, url):
        response = requests.post(TextrazorAPI.URL, {"url": url, "extractors": "words"},
                                 headers=self.headers)
        if response.status_code != 200:
            return
        try:
            sentences = response.json()["response"]["sentences"]
            self.words = set()
            for sentence in sentences:
                for word in sentence['words']:
                    if "stem" in word.keys():
                        self.words.add(word['stem'])
        except KeyError:
            return

    def best_matches(self, password):
        if len(self.words) == 0:
            return None
        best_match = None
        best_distance = float("inf")
        for word in self.words:
            best_distance, best_match = min((best_distance, best_match),
                                            (Levenshtein.distance(word.lower(), password.lower()), word.lower()))

        return best_match


if __name__ == "__main__":
    api = TextrazorAPI()
    api.get_keywords(url="https://pl.wikipedia.org/wiki/Orangutan")