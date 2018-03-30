;Gavin Harris
;CSCI 301, Lab 4

#lang racket

;type in input file name first, then output file
(define input_file (read-line))
(define output_file (read-line))

(define (AuxSum-Up n)
  ( / (* n (+ n 1)) 2)
)

;Sum-Up function, makes call to AuxSum-up to do calculation
(define (Sum-Up)
	(call-with-input-file input_file
  	(lambda (ip)
	(call-with-output-file output_file
  (lambda (op)
	(do ((c (read ip) (read ip)))
  ((eof-object? c))
	(write  c op)
  (write-string "   " op)
  (writeln (AuxSum-Up  c) op))))))
)

(define (AuxSum-Squares n)
    (cond
        ((< n 1) 0)
        (else (+ (AuxSum-Squares (- n 1)) (* n n n)))
    )
)

;Sum-Squares function, makes call to AuxSum-Squares to do calculation
(define (Sum-Squares)
	(call-with-input-file input_file
  (lambda (ip)
	(call-with-output-file output_file
  (lambda (op)
	(do ((c (read ip) (read ip)))
  ((eof-object? c))
	(write  c op)
  (write-string "   " op)
  (writeln (AuxSum-Squares  c) op))))))
)

;Symmetric-Difference function, makes call to AuxSymmetric-Difference to find the
;Symmetric Difference of two lists
(define (Symmetric-Difference)
  (call-with-input-file input_file
  (lambda (ip)
	(call-with-output-file output_file
  (lambda (op)
	(do ((c (read ip) (read ip)))
  ((eof-object? c))
	(writeln  (AuxSymmetric-Difference c (read ip)) op))))))
)

(define (AuxSymmetric-Difference L M)
	(append (difference L M) (difference M L))
)

(define (difference L M)
    (cond ( (null? L)'())
          ( (member? (car L) M)
            (difference(cdr L) M)
          )
          (else (cons (car L)
                  (difference (cdr L) M))
          )
    )
)

(define (member? e L)
    (cond ((null? L)#f)
       ((eq? e (car L))#t)
          (else (member? e (cdr L)) )
    )
)