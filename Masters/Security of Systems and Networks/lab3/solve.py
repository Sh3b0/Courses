import gmpy2

n1 = 'b74da7a827d477c636ad21c20f7d529f3c4cd90bca02ac58d5d3fe2d8950f379c7b74ba346282e7cd7529d23ec2f52d39942e722bfd67a76d2a4b538eedd2922fbb56ef5c7360540d37df38e26b3db605a98dc07c149a813ab4188c8af79d221045ebcb114bae3ea9ae27a1494864236be835dc6e942aedf6845329e5a91b289'

n2 = 'c404908d48bd0f839d4910e739107a1e32ac23e78d8eb1c10be65cb24a9da86defc424404d343ab11428f30d9770bae9cb9f6cec13781dd7a610f06ff138e2bee3c89bcd6b1f9620f386807b0bebf2c5b4062259ef36a9041ab15a7e0cfbf6ef9669d6a860f598f84b3f5c4b27d5634690121225066526cfe18ffacf487eee79'

# Convert to int
n1 = gmpy2.mpz(n1, 16)
n2 = gmpy2.mpz(n2, 16)

# Get Shared Number
p = gmpy2.gcd(n1, n2)

# Calculate the other factor
q1 = n1 / p
q2 = n2 / p

print(f"n1={n1}\n\nn2={n2}\n\np={p}\n\nq1={q1}\n\nq2={q2}")