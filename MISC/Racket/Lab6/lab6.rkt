#lang racket

(define set-equal?
    (lambda (S1 S2)
        (and (subset? S1 S2)
             ;(subset? S2 S1)
             )))

(define set-equal
    (lambda (S1 S2)
        (and (subset S1 S2)
             (subset S2 S1))))

(define subset?
    (lambda (S3 S4)
        (cond ((null? S3) #t )
        (else
         (cond ((list? (car S3)) (subset? (car S3) S4))
          (else
           (cond ((list? (car(cdr S3))) (and (member (car S3) S4)
                                             (subset? (cdr S3) (car(symmetric-difference (cons (car S3) '()) S4)))))
           (else
            (set-equal S3 S4) ))))))))

           
           ;cond true then remove instance in S4
          ; (and (member (car S3) S4)
               ; (subset? (cdr S3) (car(symmetric-difference (cons (car S3) '()) S4))))))))))
                ;(subset? (cdr S3) S4))))))))

(define subset
  (lambda (S1 S2)
    (cond ( (null? S1) #t )
          (else
           (and
            (member (car S1) S2)
            (subset (cdr S1) S2))))))

(define deep-member
  (lambda (a s)
    (or (equal? a s)
        (and (pair? s)
             (or (deep-member a (car s))
                 (deep-member a (cdr s)))))))

(define (member? e L)
    (cond ((null? L)#f)
       ((eq? e (car L))#t)
          (else (member? e (cdr L)) )
    )
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
(define (symmetric-difference L M)
	(append (difference L M) (difference M L))
)

(define (C n)
  (cond ((equal? 1 n) 1)
        (else
         (cond ((equal? 2 n) 1)
               (else 
                (C (+ (C(- n 1)) (C(- n (C(- n 1))))))
      ))))

)

(define (A n)
  (cond ((equal? 1 n) 1)
        (else
         (cond ((equal? 2 n) 1)
               (else 
                (+ (A(A(- n 1))) (A(- n (A(- n 1)))))
       )))) 
)