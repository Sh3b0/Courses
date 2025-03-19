# Ansible Demo

- Let's create a role that installs and configures `apache2` web server on my machine.

- Ansible features used:

  - `playbooks`/`inventory`/`roles` in separate directories

  - `ansible.cfg` configured to work with the specified directory structure.

  - `ansible-lint` is happy

    ![image-20250202160015516](https://i.imgur.com/oXP7cJW.png)

  - `roles/web` defines the following:

    - `tasks` to install `apache2` with `apt` and ensure the service is started and enabled,
    - Jinja2 `template` for server configuration that is copied from controller to node.
      - If the configuration changes, notify a `handler` to restart the server.
    - The template refers to an Ansible variable to configure apache2 log directory
    - The value from role `defaults` is used, unless a higher precedence value is specified (e.g., a CLI option).

- Playbook execution and testing

  ![image-20250202155030087](https://i.imgur.com/dytHdVs.png)

- Server is responding and logs are present in the specified directory.

  ![image-20250202160153568](https://i.imgur.com/6KoYEEj.png)
