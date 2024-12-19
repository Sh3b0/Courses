# keycloak-sso

Minimal login scenario with Keycloak (OIDC Provider) and Python (Flask).

- Check [REPORT.md](./REPORT.md) for more details.

## Overview

The sequence diagram illustrates the typical interactions between system components to implement a simple login scenario.

![](https://i.postimg.cc/Zncc396q/sequence-diagram.png)

## Screenshots

- Application webpage before logging in. Configured locally with self-signed certs for HTTPS.

  ![login](https://i.postimg.cc/Z50TKFTB/image.png)

- Clicking "Log In" redirects to Keyclock login form.

  - Need to pre-configure connection to the client and sample users/roles in "myrealm".

  ![keycloak](https://i.postimg.cc/VNCmXf0z/image.png)

- Successful login and redirection.

  ![success](https://i.postimg.cc/63xw0kCn/image.png)

- The client session can be inspected at the server

  ![session](https://i.postimg.cc/NM8tPdxV/image.png)

## Local Testing

0. Clone repository

   ```bash
   git clone https://github.com/sh3b0/keycloak-sso
   cd keycloak-sso
   ```

1. Configure certificates in `certs` directory. Refer to [REPORT.md](./REPORT.md#Enabling-TLS) for more details
   - Expected content: `tls.crt`, `tls.key`, and `ca.crt` (issuer CA).

2. Create `.env` with environment variables. Sample config:

   ```bash
   APP_SECRET=<APP_SECRET>
   CA_PATH=/app/certs/ca.crt
   KEYCLOAK_SERVER_URL=https://keycloak.internal.test
   KEYCLOAK_REALM=myrealm
   KEYCLOAK_CLIENT_ID=demo
   KEYCLOAK_CLIENT_SECRET=<KEYCLOAK_CLIENT_SECRET>
   KEYCLOAK_REDIRECT_URI=https://app.internal.test:5000/callback
   ```

3. Configure domain names for `app` and `keycloak` accordingly.

4. Run `keycloak` and `app` containers in the same network.

   ```bash
   docker compose up -d
   ```

5. Login to Keycloak UI with `admin:admin`, then change credentials.

6. Create a realm, a client, and sample users for testing.

7. Access the test client at port 5000.

## References

- https://www.keycloak.org/securing-apps/oidc-layers

- https://openid.net/developers/how-connect-works/

- https://openid.net/specs/openid-connect-basic-1_0.html
