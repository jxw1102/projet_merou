/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 *	Author(s) :
 *	  - Zohour Abouakil
 *    - David Courtinot
 */

package cfg.test

import ctl.ModelChecker
import cfg.ConvertNodes
import ctl.CtlExpr
import java.io.File
import ast.model.ProgramNode
import cfg.CFGMetaVar
import cfg.CFGVal
import cfg.Properties._
import ctl.BindingsEnv
import ctl.NegBinding
import cfg.DefinedString
import cfg.UndefinedVar
import cfg.MatchExprLabelizer
import cfg.CallExprPattern
import cfg.AssignmentPattern
import cfg.FindExprLabelizer
import ctl.Predicate
import ctl.EX
import ctl.Not
import ctl.MetaVariable
import ctl.Value

/**
 * This class enables to test the properties defined in cfg.Properties
 * @author Zohour Abouakil
 * @author David Courtinot
 */
object Main extends App {
	type CTL     = CtlExpr[CFGMetaVar,ProgramNode,CFGVal]
	type Checker = ModelChecker[CFGMetaVar,ProgramNode,CFGVal]
	
    /**
     * Performs the model checking and prints its results.
     * */
	def printTest(msg: String, checker: Checker, test: CTL) = println(msg + checker.evalExpr(test).mkString("\n\t","\n\t","\n"))
	
    /**
     * Performs the model checking and prints only the results without negative bindings
     * */
	def printPositiveBindings(msg: String, checker: Checker, test: CTL) = println(msg + checker.evalExpr(test).filterNot(_._2 match {
        case BindingsEnv(b) => b.count(_._2 match { case NegBinding(x) => true; case _ => false }) > 0
        case _              => false
    }).mkString("\n\t","\n\t","\n"))
    
	def loadChecker(testName: String) = {
		val file = new File("ModelChecker/unitary_tests/Model_checker/%s.cpp".format(testName))
		val name = file.getName
		val s    = name.substring(0,name.lastIndexOf('.'))
		
		val cfg       = ast.test.TestCFG.process(file.getPath,s)
		val mainGraph = cfg.decls("main")
		new ModelChecker[CFGMetaVar, ProgramNode, CFGVal](mainGraph, ConvertNodes.convert)	    
	}
	
	lazy val checker1 = loadChecker("unused_function_value")
	lazy val test1 = {
		println("Testing the FUNCTION_UNUSED_VALUE property...")
		printTest("Following lines contain unused function returned value :",checker1,FUNCTION_UNUSED_VALUE)
	}
	     
	lazy val checker2 = loadChecker("assignments")
	lazy val test2 = {
		println("Testing the AssignmentPattern...")
		printTest("Following lines are an assignment :",checker2,ASSIGNMENT)
	}
	
	lazy val test3 = {
		println("Testing the LITERAL_ASSIGNMENT property...")
		printTest("Following lines contain a literal assignment :",checker2,LITERAL_ASSIGNMENT)
	}
	
	lazy val test4 = {
		println("Testing the LITERAL_EXPR property...")
		printTest("Following lines contain are a literal expr :",checker2,LITERAL_EXPR)
	}
	
	lazy val checker3 = loadChecker("dead_code")
	lazy val test5 = {
		println("Testing the INFEASIBLE_PATH property...")
		printTest("Following lines are flow-control nodes which will be evaluated to the same value in every execution path :",
				checker3,INFEASIBLE_PATH)
	}
	
	lazy val checker4 = loadChecker("hidden_var_def")
	lazy val test6 = {
		println("Testing the HIDDEN_VAR_DEF property...")
		printTest("Following lines are variable definitions that are hidden later in the code (may contain false positive results) :",
				checker4,HIDDEN_VAR_DEF)
	}
	
	lazy val checker5 = loadChecker("arith_pointer")
	lazy val test7 = {
		println("Testing the ARITHMETIC_POINTER property...")
		printTest("Following lines contain an arithmetic expression involving a pointer :",checker5,ARITHMETIC_POINTER)
	}
	
    lazy val checker6 = loadChecker("unused_var")
    lazy val test8 = {
        println("Testing the UNUSED_DECALRED_VAR property...")
        printTest("Following lines contain variable definition that are not used :",checker6,UNUSED_DECLARED_VAR)
    }
    
    lazy val checker7 = loadChecker("file_operation")
    lazy val test9 = {
        println("Testing the NON_PAIRED_FUNCTION_CALL property...")
        printPositiveBindings("Following lines contain non-closed files :",checker7,CLOSED_RESOURCES)
    }
    
    lazy val checker8 = loadChecker("memory")
    lazy val test10 = {
        println("Testing the NEW_WITHOUT_DELETE property...")
        printPositiveBindings("Following lines will cause memory leaks :",checker8,NEW_WITHOUT_DELETE)
    }
    
    lazy val checker9 = loadChecker("cmemory")
    lazy val test11 = {
        println("Testing the NON_PAIRED_FUNCTION_CALL property...")
        printPositiveBindings("Following lines contain memory leaks :",checker9,FREED_MEMORY)
    }
    
    test11
    
}