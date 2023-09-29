from random import randint

COMMON_END = "0123456789!@#$%^&*()"
replace_signs = {
    "o": "0",
    "a": "4",
    "e": "3",
    "t": "7",
    "s": "5",
    "i": "1"
}


# this should generate limited number of variations, so that we don't get blocked from using API
def generate_variations(password: str, top: int = 10):
    top = min(20, top)
    variations = {password}
    total_changes = len(COMMON_END) + len(replace_signs.keys()) - 1
    password = password.lower()
    replacements = list(replace_signs.items())
    for i in range(top - 1):
        change = randint(0, total_changes)
        if change < len(COMMON_END):
            variations.add(password + COMMON_END[change])
        else:
            variations.add(password.replace(replacements[change - len(COMMON_END)][0],
                                            replacements[change - len(COMMON_END)][1]))

    return variations


if __name__ == "__main__":
    print(generate_variations("Hello"))
