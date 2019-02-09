import flask
from flask import Flask, request

import os
from os import path
import hashlib
import re

app = Flask(__name__)

UPLOADS = '/tmp/uploads'

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

    filename = path.join(UPLOADS, hid)
    with open(filename, 'wb') as f:
        f.write(data)

    return hid

@app.route('/file/<hid>')
def get_file(hid):
    if re.search('[^0-9a-f]', hid):
        return 'invalid hash provided'

    filename = path.join(UPLOADS, hid)
    try:
        with open(filename) as f:
            return f.read()
    except FileNotFoundError:
        return 'no file with that hash exists', 404

@app.errorhandler(404)
def not_found(error):
    return 'endpoint not found', 404

def main():
    os.makedirs(UPLOADS, exist_ok=True)

    app.run()
