package main

import (
	"fmt"
	"log"
	"net/http"
)

func main() {
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		_, err := fmt.Fprintf(w, "Hello World!")
		if err != nil {
			log.Println(err)
		}
	})
	fmt.Println("Server is listening on :8080")
	log.Fatal(http.ListenAndServe(":8080", nil))
}
