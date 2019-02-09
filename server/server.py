import flask
from flask import Flask, request

import hashlib

app = Flask(__name__)

@app.route('/')
def hello():
    return 'Hello World!'

@app.route('/create', methods=['POST'])
def create():
    data = None
    if 'file' in request.form:
        data = request.form['file'].encode('utf8')
    elif 'file' in request.files:
        datafile = request.files.get('file')
        if datafile:
            data = datafile.read()

    if not data:
        return 'invalid request', 400

    h = hashlib.sha256()
    h.update(data)
    hid = h.hexdigest()

    return hid

@app.route('/file/<hid>')
def get_file(hid):
    return 'this is a file! ' + hid

@app.errorhandler(404)
def not_found(error):
    return 'endpoint not found', 404

def main():
    app.run()
