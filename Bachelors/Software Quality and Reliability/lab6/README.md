# Lab 6 - Software Quality and Reliability

[![pipeline status](https://gitlab.com/Sh3B0/sqr-lab6/badges/main/pipeline.svg)](https://gitlab.com/Sh3B0/sqr-lab6/-/commits/main)

## Steps taken

1. Create and activate Python virtual environment, install dependencies and freeze the environment for reproducibility.

   ```bash
   python3 -m venv venv
   source venv/bin/activate
   pip3 install pytest mutatest
   pip3 freeze > requirements.txt
   ```

2. Write [`.gitignore`](./.gitignore) to prevent unneeded files from being pushed to the remote repository.

3. Use [`autopep8`](https://pypi.org/project/autopep8/) to format source files for better readability.
4. Write [`test_bonus_system.py`](./src/test_bonus_system.py) to test the function in [`bonus_system.py`](./src/bonus_system.py)
5. Update [`test_calculator.py`](./src/test_calculator.py), adding more tests to ensure full coverage.
6. Create [`.gitlab-ci.yml`](./.gitlab-ci.yml) to run mutation tests on new commits and exit with an error if at least one mutant survived.
