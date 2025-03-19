# Time App

- Sample app that shows current time for DevOps training.

## Used technology

- HTML, CSS, JS.
- Python (packages: Flask, Gunicorn).
- Docker (images: alpine, nginx).

## Endpoints

```bash
$ flask routes
Endpoint            Methods  Rule
------------------  -------  -----------------------
home                GET      /
prometheus_metrics  GET      /metrics
static              GET      /static/<path:filename>
visitor_count       GET      /visits
```

## Development

`python` and `pip` are used, make sure you have them installed and available in `$PATH` then execute the following:

```bash
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt

python app.py

# or
export FLASK_APP=app.py
export FLASK_ENV=development
flask run
```

## Testing

```bash
python -m pytest
```

## Release

```bash
export DOCKERHUB_ID=<YOUR_DOCKERHUB_ID>
export APP_NAME=app_python

# To build app image
docker build -t $DOCKERHUB_ID/$APP_NAME .

# Testing the built image locally (http://localhost:8080)
docker run -p8080:8080 $DOCKERHUB_ID/$APP_NAME

# Tag image with last commit SHA (and/or use semantic versioning)
docker tag $DOCKERHUB_ID/$APP_NAME $DOCKERHUB_ID/$APP_NAME:$(git rev-parse --short HEAD)

# Login and push image to dockerhub
docker login -u $DOCKERHUB_ID # Enter password/token when prompted
docker push $DOCKERHUB_ID/$APP_NAME --all-tags
```
