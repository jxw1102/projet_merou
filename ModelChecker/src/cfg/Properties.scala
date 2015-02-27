package cfg

import java.io.File
import scala.reflect.runtime.universe
import ctl._
import ast.ProgramNode
import ctl.TrueLabelizer

object Properties extends App {
//	val INFEASIBLE_PATH  = Predicate(InfeasiblePathLabelizer())
//	val ARITH_POINTER    = Predicate(ArithmeticPointerLabelizer())
//    val UNUSED_VAR       = (Predicate(VarDeclLabelizer(VarDeclPattern(None, UndefinedVar("X")))) 
//              && AX(AG(Predicate(UnusedLabelizer(UndefinedVar("X"))))))
//    val UNUSED_FUNCTION_VALUE = Predicate(UnusedFunctionValue())
//    // Incorrect !! To debug...
//    val HIDDEN_VAR_DEF  = (Predicate(VarDefLabelizer(VarDeclPattern(None, UndefinedVar("X")))) 
//    		&& EX(EF(Predicate(VarDefLabelizer(VarDeclPattern(None, UndefinedVar("X")))))))
//    val FIND_FUNCTION = Predicate(FindExprLabelizer(CallExprPattern(UndefinedVar("X"))))
    val FIND_FUNCTION_PARAMS = Predicate(FindExprLabelizer(CallExprPattern(UndefinedVar("X"), Some(List(UndefinedVar("Y"),UndefinedVar("Z"))))))

    val FUNCTION_USE_VALUE   = (Predicate(FindExprLabelizer(CallExprPattern(UndefinedVar("Z"),None,NotString(Set("void")))))
                                && 
                               Exists(("X",new NoType),Predicate(ExpressionLabelizer(UndefinedVar("X"))))
                               &&
                               Not(Exists(("X",new NoType), 
                                       (Exists(("Y",new NoType[CFGVal]),
                                       Predicate(ExpressionLabelizer(BinaryOpPattern(UndefinedVar("X"),UndefinedVar("Y"), "=")))))))
                               &&
                               Not(Exists(("X",new NoType), 
                                       (Exists(("Y",new NoType[CFGVal]),
                                       Predicate(ExpressionLabelizer(CompoundAssignOpPattern(UndefinedVar("X"),UndefinedVar("Y")))))))))
    
                                       
                                       
//	val test = "arith_pointer"
	val test = "unused_function_value"
//	val test = "hidden_def"
//    val test = "dead_code"
	    
	val file = new File("ModelChecker/unitary_tests/Model_checker/%s.cpp".format(test))
	val name = file.getName
	val s    = name.substring(0,name.lastIndexOf('.'))
	
	val cfg       = ast.test.TestCFG.process(file.getPath,s)
	val mainGraph = cfg.decls("main")
	val checker   = new ModelChecker[CFGMetaVar, ProgramNode, CFGVal](mainGraph, ConvertNodes.convert)
	
//	println(checker.evalExpr(UNREACHABLE_CODE))
//	println(checker.evalExpr(INFEASIBLE_PATH))
//	println(checker.evalExpr(ARITH_POINTER))
//	println(checker.evalExpr(UNUSED_FUNCTION_VALUE))
//	println(checker.evalExpr(HIDDEN_VAR_DEF))
	
//    println(checker.evalExpr(UNUSED_VAR))
//    println("\ndecl : " + checker.evalExpr(Predicate(VarDeclLabelizer(VarDeclPattern(None, UndefinedVar("X"))))))
//    println("\nunused : " + checker.evalExpr(EF(AX(Predicate(UnusedLabelizer(UndefinedVar("X")))))))
//    println(checker.evalExpr(Predicate(VarDeclLabelizer(VarDeclPattern(None, UndefinedVar("X")))) &&
//            AX(Not(EF(Not(Predicate(UnusedLabelizer(UndefinedVar("X")))))))))
//	println(checker.evalExpr(ARITH_POINTER))
//  println(checker.evalExpr(UNUSED_VAR))
//  println(checker.evalExpr(REDEFINE_VAR))
//    println(checker.evalExpr(FIND_FUNCTION_PARAMS).size)

    println(checker.evalExpr(FUNCTION_USE_VALUE))
}

