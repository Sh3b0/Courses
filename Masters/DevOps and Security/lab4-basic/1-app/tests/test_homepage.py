"""
Functional test for the application homepage
"""
from app import app


def test_home_page():
    """
    GIVEN a Flask application configured for testing,
    WHEN the '/' page is requested (GET),
    THEN check that the response is valid
    """

    with app.test_client() as test_client:
        response = test_client.get("/")
        assert response.status_code == 200
        assert b"time" in response.data
