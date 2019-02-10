import flask
from flask import Flask, request

import os
from os import path
import subprocess
import hashlib
import re

from ..model import model

app = Flask(__name__)

UPLOADS = './uploads'
MODELS = './models'

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
        return 'file field not provided', 400

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
        with open(filename, 'rb') as f:
            return f.read()
    except FileNotFoundError:
        return 'no file with that hash exists', 404

@app.route('/model/<hid>')
def get_model(hid):
    if re.search('[^0-9a-f]', hid):
        return 'invalid hash provided'

    return get_model(hid)

@app.route('/render/<hid>')
def get_render(hid):
    if re.search('[^0-9a-f]', hid):
        return 'invalid hash provided'

    return get_render(hid)

@app.errorhandler(404)
def not_found(error):
    return 'endpoint not found', 404

def main():
    os.makedirs(UPLOADS, exist_ok=True)
    os.makedirs(MODELS, exist_ok=True)

    app.run()

def get_model(hid):
    model_filename = path.join(MODELS, hid)
    if path.exists(model_filename):
        with open(model_filename, 'rb') as mf:
            return mf.read()
    else:
        upload_filename = path.join(UPLOADS, hid)

        if path.exists(upload_filename):
            mod = model.create_model(bytes.fromhex(hid), 3, 9)
            mod = model.render_model(mod).encode('utf8')
            model_filename = path.join(MODELS, hid)
            with open(model_filename, 'wb') as mf:
                mf.write(mod)
            return mod

    return None

def get_render(hid):
    if not get_model(hid):
        return None

    render_filename = path.join(MODELS, hid + '.stl')
    if not path.exists(render_filename):
        model_filename = path.join(MODELS, hid)
        subprocess.run(['openscad', model_filename, '-o', render_filename])

    with open(render_filename, 'rb') as mf:
        return mf.read()
