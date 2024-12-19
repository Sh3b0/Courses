cmake_minimum_required(VERSION 3.16)
project(main)

set(CMAKE_CXX_STANDARD 11)

add_executable(main main.cpp errors.h log_manager.h matrix/matrix.tpp)
