from main import app

def test_index():
    with app.test_client() as test_client:
        response = test_client.get("/")
        assert response.status_code == 200
        assert b"time" in response.data
