package ctl.experiment.generics

import ast.ProgramNodeLabelizer

/**
 * @author Zohour Abouakil
 * @author Fabien Sauce
 */
// Parent class of all the class used in CTL 
sealed abstract class CtlExprTest[T] {
    def AU(that: CtlExprTest[T])  = _AU(this,that)
    def EU(that: CtlExprTest[T])  = _EU(this,that)
    def &&(that : CtlExprTest[T]) = And(this,that)
    def ||(that : CtlExprTest[T]) = Or (this,that)
    def unary_!            = Not(this)
}

// Binary operators
final case class And[T]  (left: CtlExprTest[T], right: CtlExprTest[T]) extends CtlExprTest[T] 
final case class Or[T]   (left: CtlExprTest[T], right: CtlExprTest[T]) extends CtlExprTest[T] 
final case class _AU[T]  (left: CtlExprTest[T], right: CtlExprTest[T]) extends CtlExprTest[T] 
final case class _EU[T]  (left: CtlExprTest[T], right: CtlExprTest[T]) extends CtlExprTest[T] 

// Unary operators
final case class AX[T]                  (op  : CtlExprTest[T]) extends CtlExprTest[T] 
final case class EX[T]                   (op  : CtlExprTest[T]) extends CtlExprTest[T] 
final case class AG[T]                   (op  : CtlExprTest[T]) extends CtlExprTest[T] 
final case class EG[T]                   (op  : CtlExprTest[T]) extends CtlExprTest[T] 
final case class AF[T]                   (op  : CtlExprTest[T]) extends CtlExprTest[T] 
final case class EF[T]                   (op  : CtlExprTest[T]) extends CtlExprTest[T] 
final case class Not[T]                  (op  : CtlExprTest[T]) extends CtlExprTest[T] 
final case class Exists[T]  (name: String, op  : CtlExprTest[T]) extends CtlExprTest[T]

// Predicate
final case class Predicate[T]  (label: Labelizer[T]) extends CtlExprTest[T]
abstract class Labelizer[T] {
    def test(t: T): EnvironmentTest[T]
}