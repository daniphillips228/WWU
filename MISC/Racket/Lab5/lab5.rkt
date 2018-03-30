#lang racket
;Gavin Harris
;CSCI 301 Lab 5

(define (Symmetric? L)
  (Symmetric2 L L)
)
(define (Symmetric2 L1 L2)
  (cond ((null? L1)#t)
        (else 
  (SymmetricAux (car L1)(cdr L1) L2 L2)))
)  
  
(define (SymmetricAux S1 S2 C L)
  ;if we've checked all
    (cond ((null? C)#f)
          (else
           ;special case ex (1 1)
           (cond ((eqv? (car S1) (car(cdr S1)))#t)
           (else
           (cond ((equal? S1 (car C))(SymmetricAux S1 S2 (cdr C) L))                 
                  (else
                   (cond ((set-equal? S1 (car C)) (Symmetric2 S2  L))
                         (else
                          (SymmetricAux S1 S2 (cdr C) L))
           ))))))
    )
 )

(define (AntiTransitive? L)
  (AntiTransitive2 L L)
)
(define (AntiTransitive2 L1 L2)
  (cond ((null? L1)#t)
        (else 
  (AntiTransitiveAux (car L1)(cdr L1) L2 L2 L2)))
)  
  
(define (AntiTransitiveAux S1 S2 C L C2)
  ;if we've checked all
          (cond ((empty? C)#t)

          (else
           ;special case ex (1 1)
           (cond ((equal? (car (cdr S1)) (car (car C)))
                  (cond((or (empty? C2) (empty? (car C2)))#t)
                       (else
                  (cond((equal? (list (car S1) (car (cdr (car C)))) (car C2))#f)                 

                         (else
                          (AntiTransitiveAux S1 S2 C L (cdr C2)))
           ))))
                 (else
                          (AntiTransitiveAux S1 S2 (cdr C) L C2))
                 ))
    )
 )

 (define (Reflexive? L S)

    (cond ((null? S)#t)
          (else
           (cond ((ReflexiveAux L (car S))
              (Reflexive? L (cdr S)))
                 (else
                  (eq? 1 0))
           )

          )
    )
 )
 (define (ReflexiveAux L S1)

    (cond ((empty? L)#f)
    	(else
         (cond ((eqv? (car(car L)) S1)
                (cond((eqv? (car(cdr(car L))) S1)#t)
                     (else
                      (cond ((empty? (cdr L))#f)
               (else
        	(ReflexiveAux (cdr L) S1 )
                ))
    	)))
    	(else

         (cond ((empty? (cdr L))#f)
               (else
        	(ReflexiveAux (cdr L) S1 )
                ))
    	)))
    )
    
 )
(define (Transitive? L)
  (Transitive2 L L)
)
(define (Transitive2 L1 L2)
  (cond ((null? L1)#f)
        (else 
  (TransitiveAux (car L1)(cdr L1) L2 L2 L2)))
)  
  
(define (TransitiveAux S1 S2 C L C2)
  ;if we've checked all
          (cond ((empty? C)#f)

          (else
           ;special case ex (1 1)
           (cond ((equal? (car (cdr S1)) (car (car C)))
                  (cond((or (empty? C2) (empty? (car C2)))#f)
                       (else
                  (cond((equal? (list (car S1) (car (cdr (car C)))) (car C2))#t)                 

                         (else
                          (TransitiveAux S1 S2 C L (cdr C2)))
           ))))
                 (else
                          (TransitiveAux S1 S2 (cdr C) L C2))
                 ))
    )
 )

(define set-equal?
(lambda (S1 S2)
(and
(subset? S1 S2)
(subset? S2 S1))))

(define subset?
(lambda (S1 S2)
(cond ( (null? S1) #t )
(else
(and
(member (car S1) S2)
(subset? (cdr S1) S2))))))

(define (member? e L)
    (cond ((null? L)#f)
       ((eq? e (car L))#t)
          (else (member? e (cdr L)) )
    )
)