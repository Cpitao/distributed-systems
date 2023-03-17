from hashlib import sha1


def get_first_sha1(password: str, first: int = 5):
    hash_object = sha1(bytes(password, 'utf-8'))
    return hash_object.hexdigest()[:first]


def verify_hash(h: bytes, password: str):
    if h == sha1(bytes(password, 'utf-8')).hexdigest():
        return True
    return False


if __name__ == "__main__":
    print(get_first_sha1("123456"))