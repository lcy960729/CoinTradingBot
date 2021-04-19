import jwt   # PyJWT 
import uuid
import hashlib
import json
from urllib.parse import urlencode

accessKey = ''
secretKey = ''

with open('key.json') as json_file:
    json_data = json.load(json_file)

    accessKey = json_data['accessKey']
    secretKey = json_data['secretKey']

def makePayload(query):
    payload = {
    'access_key': accessKey,
    'nonce': str(uuid.uuid4()),
    }

    if (query != None):
        query_string = urlencode(query).encode()

        m = hashlib.sha512()
        m.update(query_string)
        query_hash = m.hexdigest()

        payload['query_hash'] = query_hash
        payload['query_hash_alg'] = 'SHA512'

    return payload


def get(query = None):
    jwt_token = jwt.encode(makePayload(query), secretKey)
    authorization_token = 'Bearer {}'.format(jwt_token)
    
    return authorization_token

