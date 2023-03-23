Application allows to verify if a password or it's variations have been leaked using HIBP API (https://haveibeenpwned.com/API/v3).
Number of created variations is limited, so that the app doesn't get blocked from using API by sending too many requests.

The user can also find out whether the provided password could be easily derived from some words e.g. passw0rd can be easily derived from the word password. For this the Datamuse API is used.

Lastly, the app uses TextRazor API for extracting keywords from given URL. This could be particularly useful for company pages and finding out if possible attackers could get our password by scraping the webpage.
