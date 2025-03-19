import requests
from os import environ
from sys import argv

headers = {
    'Authorization': f'Token {environ["DD_API_TOKEN"]}'
}

url = 'http://defectdojo.duckdns.org/api/v2/import-scan/'

data = {
    'active': True,
    'verified': True,
    'scan_type': 'SARIF',
    'auto_create_context': True,
    'product_name': 'demo',
    'engagement_name': 'CI Engagement',
    'test_title': 'CI Scan',
}

files = {
    'file': open(argv[1], 'r')
}

res = requests.post(url, headers=headers, data=data, files=files)

if res.status_code == 201:
    print(f"Report {argv[1]} uploaded successfully!")
else:
    print(f"Report {argv[1]} failed to upload.\n{res.content}")
