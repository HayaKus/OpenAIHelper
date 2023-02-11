import requests

from flask import Flask
from flask_restful import reqparse, Api, Resource


app = Flask(__name__)
api = Api(app)

parser = reqparse.RequestParser()
parser.add_argument('prompt')

class Chat(Resource):

    def post(self):
        args = parser.parse_args()
        print(args)

        prompt = args['prompt']

        # API endpoint
        endpoint = "https://api.openai.com/v1/completions"

        # API key
        api_key = "YOUR API_KEY"

        # API request data
        data = {"model": "text-davinci-003", "prompt": f"{prompt}", "temperature": 0, "max_tokens": 4000}

        # API request headers
        headers = {
            "Content-Type": "application/json",
            "Authorization": f"Bearer {api_key}"
        }

        # Send API request
        response = requests.post(endpoint, headers=headers, json=data)

        # Check response status code
        if response.status_code == 200:
            # Extract response text
            response_text = response.json()["choices"][0]["text"]
            print(response_text)
        else:
        # Handle error
            print("An error occurred:", response.text)
        return response_text, 200
        



api.add_resource(Chat, '/chat')

if __name__ == '__main__':
    app.run(debug=True)
