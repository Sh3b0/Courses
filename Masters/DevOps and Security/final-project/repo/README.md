# defectdojo-demo

1. Deploy DefectDojo server with docker compose.

   ```bash
   git clone https://github.com/DefectDojo/django-DefectDojo
   cd django-DefectDojo
   docker compose build && docker compose up -d
   docker compose logs -f initializer | grep "Admin password:"
   ```

1. Clone sample projects

   ```bash
   git clone https://github.com/hardik05/Damn_Vulnerable_C_Program
   mv Damn_Vulnerable_C_Program dvca
   rm -rf dvca/.git
   
   git clone https://github.com/appsecco/dvna
   rm -rf dvna/.git
   
   git clone https://github.com/fportantier/vulpy
   rm -rf vulpy/.git
   ```

1. Add `.gitlab-ci.yml` and `upload_reports.py`

   - Change server URL in the script if needed.
   - Provide API token `DD_API_TOKEN` in GitLab CI variables.

1. Push content to a newly-created GitLab repo to run the pipelines

1. Inspect results in DefectDojo server

   ![image-20250301233812230](https://i.ibb.co/8nsx0H55/image.png)

   ![image-20250301232643942](https://i.ibb.co/5XwkRj3c/image.png)

