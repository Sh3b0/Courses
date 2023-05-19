# Open a file for reading
with open('example.txt', 'r') as file:
    # Read the contents of the file
    contents = file.read()
    # Print the contents of the file
    print(contents)

# Open a file for writing
with open('output.txt', 'w') as file:
    # Write some text to the file
    file.write('Hello, world!')

