#lang racket
;Gavin Harris
;Lab7

(define (Bag-Sum B1 B2)
  (append B1 B2)
)

(define count
  (lambda (a L)
    (cond ( (null? L) 0 )
          ( (eq? a (car L))
            (+ 1 (count a (cdr L))))
          (else (count a (cdr L)))
 )))

(define (Bag-Union B1 B2)
  (UnionAux B1 B2 B1
            (cond ( (> (count (car B1) B1) (count (car B1) B2)) (insert (car B1) '() (count (car B1) B1)) )
                  (else
                   (insert (car B1) '() (count (car B1) B2))
                   )
             )
   )
)

(define (UnionAux B1 B2 B3 Bag)
  (cond ((empty? B3) Bag)
        (else
         (cond ( ( member? (car B3) Bag ) (UnionAux B1 B2 (cdr B3) Bag) )
               (else
                (cond ( (> (count (car B3) B1) (count (car B3) B2)) (UnionAux B1 B2 (cdr B3)(insert (car B3) Bag (count (car B3) B1))) )
                      (else
                       (UnionAux B1 B2 (cdr B3) (insert (car B3) Bag (count (car B3) B2)))
                )      )
   ))
)))

(define (Bag-Intersection B1 B2)
  (IntersectionAux B1 B2 B1
                   (cond ( (< (count (car B1) B1) (count (car B1) B2)) (insert (car B1) '() (count (car B1) B1)) )
                         (else
                          (insert (car B1) '() (count (car B1) B2))
                          )
                   )                
  )
)

(define (IntersectionAux B1 B2 B3 Bag)
  (cond ((empty? B3) Bag)
        (else
         (cond ( ( member? (car B3) Bag ) (IntersectionAux B1 B2 (cdr B3) Bag) )
               (else
                (cond ( (< (count (car B3) B1) (count (car B3) B2)) (IntersectionAux B1 B2 (cdr B3)(insert (car B3) Bag (count (car B3 ) B1))) )
                      (else
                       (IntersectionAux B1 B2 (cdr B3) (insert (car B3) Bag (count (car B3) B2)))
                       ))
   ))))
)
  
(define insert
  (lambda (ob L n)
    (cond ( (eq? n 0) L )
          (else
           (insert ob (cons ob L) (- n 1))))))

(define (member? e L)
    (cond ((null? L)#f)
       ((eq? e (car L))#t)
          (else (member? e (cdr L)) )
    )
)