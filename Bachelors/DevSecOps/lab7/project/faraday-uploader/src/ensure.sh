#!/usr/bin/env bash

ensure::faraday_cli_command_exists() {
  CMDS="faraday-cli"

  echo "Executing Faraday import"

  for i in $CMDS
  do
    command -v $i >/dev/null && continue || { echo "$i command not found. Install the missing command and try again"; exit 1; }
  done
}

ensure::minimun_args() {
  local -r received_args=$(( $# - 1 ))
  local -r minimun_expected_args=$1

  if ((received_args < minimun_expected_args)); then
    echo "Illegal number of parameters, $minimun_expected_args at least expected but $received_args found"
    exit 1
  fi
}