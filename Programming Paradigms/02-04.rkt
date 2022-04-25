#lang racket

; 2.1
(define (alternating-sum lst)
  (if (empty? lst) 0 (- (first lst) (alternating-sum (rest lst))))
  )

> (alternating-sum '(6 2 4 1 3 9)) ; 1
 ; (6 - (2 - (4 - (1 - (3 - 9))))) = 1
 ; 6 - 2 + 4 - 1 + 3 - 9 = 1

; 2.2
(define (dec n) (- n 1))
(define (f n)
	(cond [(<= n 2) (- 3 n)] [else (+ (f (dec n)) (f (dec (dec n))))]))
; (f 3)
; (+ (f (dec 3)) (f (dec (dec 3))))
; (+ (f 2) (f 1))
; (+ (- 3 2) (- 3 1))
; (+ 1 2)
; 3


; 3.1
(define employees
    '(("John" "Malkovich" . 29)
    ("Anna" "Petrova" . 22)
    ("Ivan" "Ivanov" . 23)
    ("Anna" "Karenina" . 40)))

(define (fullname emp)
  (cons (car emp) (car (cdr emp))))

> (fullname '("John" "Malkovich" . 29))

; 3.2
> (filter (lambda (x) (equal? (car x) "Anna")) employees)


; 3.3
(define (employees-over-25 emps)
  (map fullname (filter (lambda (x) (> (cdr (cdr x)) 25)) emps)))

> (employees-over-25 employees)

; 4.1
(define (replicate n x)
  (if (equal? n 0) empty (cons x (replicate (- n 1) x))))

> (replicate 10 'a)

; 4.2
(define (prepend e lst)
  (if (empty? lst) (list (cons e 1))
      (if (equal? (car (first lst)) e)
          (append (list (cons (car (first lst)) (+ 1 (cdr (first lst))))) (rest lst))
          (append (list (cons e 1)) lst))))

(define (encode lst)
  (if (empty? lst) empty (prepend (first lst) (encode (rest lst)))))

> (encode '(a a b c c c a a a))

; 4.3

(define (decode lst)
  (map (lambda (p) (replicate (cdr p) (car p))) lst)
  )
> (decode '((a . 2) (b . 1) (c . 3) (a . 3)))
