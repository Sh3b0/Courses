#!/bin/bash

if [ "$#" -ne 1 ] || [ ! -d "$1" ]; then
    echo "usage: ./program.sh <dir>"
    exit
fi

declare -A libs_x86 libs_x86_64 libs_aarch64 libs_arm
while IFS= read -r -d '' program; do
  for lib in $(objdump -p "$program" 2> /dev/null | grep NEEDED | awk '{print $2}'); do
      if file -0 "$program" | cut -d '' -f2 | grep -q 'Intel 80386'; then
        libs_x86[$lib]+=$'\t<= '$program$'\r\n'
      elif file -0 "$program" | cut -d '' -f2 | grep -q 'x86-64'; then
        libs_x86_64[$lib]+=$'\t<= '$program$'\r\n'
      elif file -0 "$program" | cut -d '' -f2 | grep -q 'ARM aarch64'; then
        libs_aarch64[$lib]+=$'\t<= '$program$'\r\n'
      elif file -0 "$program" | cut -d '' -f2 | grep -q 'ARM'; then
        libs_arm[$lib]+=$'\t<= '$program$'\r\n'
      fi
  done
done < <(find "$1/" -type f -executable -print0 2> /dev/null)

if [ ${#libs_x86[@]} -ne 0 ]; then
  printf "x86 binaries\n===================\n"
  while read -r line; do
      printf "%s\n%s\n" "$line" "${libs_x86[$(echo "$line" | cut -d' ' -f1)]}"
  done < <(
    for key in "${!libs_x86[@]}"; do
            printf "%s ( %d exes )\n" "$key" "$(tr -dc '=' <<< "${libs_x86[$key]}" | awk '{ print length; }')"
    done | sort -rn -k3
  )
fi

if [ ${#libs_x86_64[@]} -ne 0 ]; then
  printf "x86-64 binaries\n===================\n"
  while read -r line; do
      printf "%s\n%s\n" "$line" "${libs_x86_64[$(echo "$line" | cut -d' ' -f1)]}"
  done < <(
    for key in "${!libs_x86_64[@]}"; do
            printf "%s ( %d exes )\n" "$key" "$(tr -dc '=' <<< "${libs_x86_64[$key]}" | awk '{ print length; }')"
    done | sort -rn -k3
  )
fi

if [ ${#libs_aarch64[@]} -ne 0 ]; then
  printf "aarch64 binaries\n===================\n"
  while read -r line; do
      printf "%s\n%s\n" "$line" "${libs_aarch64[$(echo "$line" | cut -d' ' -f1)]}"
  done < <(
    for key in "${!libs_aarch64[@]}"; do
            printf "%s ( %d exes )\n" "$key" "$(tr -dc '=' <<< "${libs_aarch64[$key]}" | awk '{ print length; }')"
    done | sort -rn -k3
  )
fi

if [ ${#libs_arm[@]} -ne 0 ]; then
  printf "arm binaries\n===================\n"
  while read -r line; do
      printf "%s\n%s\n" "$line" "${libs_arm[$(echo "$line" | cut -d' ' -f1)]}"
  done < <(
    for key in "${!libs_arm[@]}"; do
            printf "%s ( %d exes )\n" "$key" "$(tr -dc '=' <<< "${libs_arm[$key]}" | awk '{ print length; }')"
    done | sort -rn -k3
  )
fi