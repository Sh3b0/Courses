import argparse

# Create the parser object
parser = argparse.ArgumentParser(description='A minimal example of argparse usage.')

# Add a positional argument
parser.add_argument('input_file', help='Input file path')

# Add an optional argument
parser.add_argument('-o', '--output_file', help='Output file path')

# Parse the command line arguments
args = parser.parse_args()

# Print the input file path
print(f'Input file: {args.input_file}')

# Print the output file path if provided
if args.output_file:
    print(f'Output file: {args.output_file}')

