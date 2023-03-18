import requests


# Have I Been PWNed API class
class HibpAPI:
    BASE_URL = "https://api.pwnedpasswords.com/range/"

    headers = {
        "user-agent": "Password variations checker"
    }

    def query_api(self, hash_first_5_letters):
        response = requests.get(HibpAPI.BASE_URL + hash_first_5_letters, headers=HibpAPI.headers,
                                timeout=2)
        if response.status_code != 200:
            return None
        content = response.content.decode()
        result = {}
        for line in content.split():
            ending, count = line.split(sep=':')
            try:
                count = int(count)
            except ValueError:
                return None
            result[ending] = count
        return result



if __name__ == "__main__":
    api = HibpAPI()
    import hasher
    print(api.query_api(hasher.get_first_sha1(password="123456")))