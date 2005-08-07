/*
 * Copyright 2004 Davy Verstappen
 * Portions Copyright (C) 2003 Vivek Gupta
 * Portions Copyright (C) 2005 Nigel Sheridan-Smith
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

header {
package org.jsmiparser.phase.file.antlr;

import org.jsmiparser.smi.*;
import org.jsmiparser.phase.file.*;

import antlr.*;
import java.lang.* ;
import java.math.*;
import java.util.*;
}

//	Creation of ASN.1 grammar for ANTLR	V2.7.1
// ===================================================
//		  TOKENS FOR ASN.1 LEXER DEFINITIONS
// ===================================================

class SMILexer extends Lexer;
options	{
	k =	3;
	exportVocab=SMI;
	charVocabulary = '\3'..'\377';
	caseSensitive=true;
	testLiterals = true;
	codeGenMakeSwitchThreshold = 2;  // Some optimizations
	codeGenBitsetTestThreshold = 3;
}

//	ASN1 Tokens 

tokens {
	
ABSENT_KW = "ABSENT" ;
ABSTRACT_SYNTAX_KW = "ABSTRACT-SYNTAX" ;
ALL_KW= "ALL" ;
ANY_KW = "ANY" ;
ARGUMENT_KW = "ARGUMENT" ;
APPLICATION_KW = "APPLICATION" ;
AUTOMATIC_KW = "AUTOMATIC" ;
BASED_NUM_KW = "BASEDNUM" ;
BEGIN_KW = "BEGIN" ;
BIT_KW = "BIT" ;
BMP_STRING_KW = "BMPString" ;
BOOLEAN_KW = "BOOLEAN" ;
BY_KW = "BY" ;
CHARACTER_KW = "CHARACTER" ;
CHOICE_KW = "CHOICE" ;
CLASS_KW = "CLASS" ;
COMPONENTS_KW = "COMPONENTS" ;
COMPONENT_KW = "COMPONENT" ;
CONSTRAINED_KW = "CONSTRAINED" ;
DEFAULT_KW = "DEFAULT" ;
DEFINED_KW = "DEFINED" ;
DEFINITIONS_KW = "DEFINITIONS" ;
EMBEDDED_KW = "EMBEDDED" ;
END_KW = "END" ;
ENUMERATED_KW = "ENUMERATED" ;
ERROR_KW = "ERROR" ;
ERRORS_KW = "ERRORS" ;
EXCEPT_KW = "EXCEPT" ;
EXPLICIT_KW = "EXPLICIT" ;
EXPORTS_KW = "EXPORTS" ;
EXTENSIBILITY_KW = "EXTENSIBILITY" ;
EXTERNAL_KW = "EXTERNAL" ;
FALSE_KW = "FALSE" ;
FROM_KW = "FROM" ;
GENERALIZED_TIME_KW = "GeneralizedTime" ;
GENERAL_STR_KW = "GeneralString" ;
GRAPHIC_STR_KW = "GraphicString" ;
IA5_STRING_KW = "IA5String" ;
IDENTIFIER_KW = "IDENTIFIER" ;
IMPLICIT_KW = "IMPLICIT" ;
IMPLIED_KW = "IMPLIED" ;
IMPORTS_KW = "IMPORTS" ;
INCLUDES_KW = "INCLUDES" ;
INSTANCE_KW = "INSTANCE" ;
INTEGER_KW = "INTEGER" ;
INTERSECTION_KW = "INTERSECTION" ;
ISO646STRING_KW = "ISO646String" ;
LINKED_KW = "LINKED" ;
MAX_KW = "MAX" ;
MINUS_INFINITY_KW = "MINUSINFINITY" ;
MIN_KW = "MIN" ;
NULL_KW = "NULL" ;
NUMERIC_STR_KW = "NumericString" ;
OBJECT_DESCRIPTOR_KW = "ObjectDescriptor" ;
OBJECT_KW = "OBJECT" ;
OCTET_KW = "OCTET" ;
OPERATION_KW = "OPERATION" ;
OF_KW = "OF" ;
OID_KW = "OID" ;
OPTIONAL_KW = "OPTIONAL" ;
PARAMETER_KW = "PARAMETER" ;
PDV_KW = "PDV" ;
PLUS_INFINITY_KW = "PLUSINFINITY" ;
PRESENT_KW = "PRESENT" ;
PRINTABLE_STR_KW = "PrintableString" ;
PRIVATE_KW = "PRIVATE" ;
REAL_KW = "REAL" ;
RELATIVE_KW = "RELATIVE" ;
RESULT_KW = "RESULT" ;
SEQUENCE_KW = "SEQUENCE" ;
SET_KW = "SET" ;
SIZE_KW = "SIZE" ;
STRING_KW = "STRING" ;
TAGS_KW = "TAGS" ;
TELETEX_STR_KW = "TeletexString" ;
TRUE_KW = "TRUE" ;
TYPE_IDENTIFIER_KW = "TYPE-IDENTIFIER" ;
UNION_KW = "UNION" ;
UNIQUE_KW = "UNIQUE" ;
UNIVERSAL_KW = "UNIVERSAL" ;
UNIVERSAL_STR_KW = "UniversalString" ;
UTC_TIME_KW = "UTCTime" ;
UTF8STRING_KW = "UTF8String" ;
VIDEOTEX_STR_KW = "VideotexString" ;
VISIBLE_STR_KW = "VisibleString" ;
WITH_KW = "WITH" ;
}

// Operators

ASSIGN_OP:	"::=";
BAR:		'|';
COLON:		':';
COMMA:		',';
COMMENT:	"--";
DOT:		'.';
DOTDOT:		"..";
ELLIPSIS:	"...";
EXCLAMATION:	'!';
INTERSECTION:	'^';
LESS:		'<';
L_BRACE:	'{';
L_BRACKET:	'[';
L_PAREN:	'(';
MINUS:		'-';
PLUS:		'+';
R_BRACE:	'}';
R_BRACKET:	']';
R_PAREN:	')';
SEMI:		';';
SINGLE_QUOTE:	"'";
CHARB:		"'B";
CHARH:		"'H";

// Whitespace -- ignored

WS			
:
	( ' '
	| '\t'
	| '\f'
	|	( options {generateAmbigWarnings=false;}
		:
			"\r\n"	{ newline(); }// DOS
			| '\r'  { newline(); }// Macintosh
			| '\n'	{ newline(); }// Unix 
		)
	)+
{
	$setType(Token.SKIP);
}
;

// Single-line comments
SL_COMMENT
	: (options {warnWhenFollowAmbig=false;} 
	: COMMENT (  { LA(2)!='-' }? '-' 	|	~('-'|'\n'|'\r'))*	( (('\r')? '\n') { newline(); }| COMMENT) )
		{$setType(Token.SKIP);  }
	;

NUMBER	:	('0'..'9')+ ;

UPPER	
options {testLiterals = false;}
	:   ('A'..'Z') 
		(options {warnWhenFollowAmbig = false;}
	:	( 'a'..'z' | 'A'..'Z' |'-' | '0'..'9' ))*
;

LOWER
options {testLiterals = false;}
	:	('a'..'z') 
		(options {warnWhenFollowAmbig = false;}
	:	( 'a'..'z' | 'A'..'Z' |'-' | '0'..'9' ))*
;


protected
BDIG		: ('0'|'1') ;
protected
HDIG		:	(options {warnWhenFollowAmbig = false;} :('0'..'9') )
			|	('A'..'F')
			|	('a'..'f')
			;

// Unable to resolve a string like 010101 followed by 'H
//B_STRING 	: 	SINGLE_QUOTE ({LA(3)!='B'}? BDIG)+  BDIG SINGLE_QUOTE 'B';
//H_STRING 	: 	SINGLE_QUOTE ({LA(3)!='H'}? HDIG)+  HDIG SINGLE_QUOTE 'H';

B_OR_H_STRING
	:	(options {warnWhenFollowAmbig = false;} 
		:(B_STRING)=>B_STRING {$setType(B_STRING);}
		| H_STRING {$setType(H_STRING);})
	;

/* Changed by NSS 13/1/05 - upper case *or* lower case 'B' and 'H'; zero or more digits */
protected
B_STRING 	: 	SINGLE_QUOTE (BDIG)* SINGLE_QUOTE ('B' | 'b') ;
protected
H_STRING 	: 	SINGLE_QUOTE (HDIG)* SINGLE_QUOTE ('H' | 'h') ;

			
C_STRING 	: 	'"' (options {greedy=false;}
                             : "\r\n"		{ newline(); }// DOS
                             | '\r'   		{ newline(); }// Macintosh
                             | '\n'		{ newline(); }// Unix 
                             | ~('\r' | '\n')
                            )* 
                        '"' ;


//*************************************************************************
//**********		PARSER DEFINITIONS
//*************************************************************************


class SMIParser	extends	Parser;
options	{
	exportVocab=SMI;
	k=3;
        defaultErrorHandler=false;
}


module_definition
:
	module_identifier DEFINITIONS_KW ASSIGN_OP
	BEGIN_KW
		module_body
	END_KW
;

module_identifier
:
	u:UPPER
{
	SkipStandardException.skip(u.getText());
}
;

module_body
:
	(imports)?
	(assignment)*
;


imports
:
	IMPORTS_KW (symbols_from_module)* SEMI
;

symbols_from_module
:
	symbol_list FROM_KW UPPER 
;

symbol_list
:
	symbol (COMMA symbol)*
;

symbol
:
	UPPER
	| integer_type_kw
	| LOWER
	| macroName
;

macroName
:
	"OBJECT-TYPE"
	| "MODULE-IDENTITY"
	| "OBJECT-IDENTITY"
	| "NOTIFICATION-TYPE" 
        | "TEXTUAL-CONVENTION"
	| "OBJECT-GROUP"
	| "NOTIFICATION-GROUP"
	| "MODULE-COMPLIANCE" 
	| "AGENT-CAPABILITIES"
	| "TRAP-TYPE"
;


assignment
:
	UPPER ASSIGN_OP type_assignment
	| LOWER value_assignment
;

type_assignment
:
	textualconvention_macro
	| leaf_type
	| sequence_type
;

// valid type for a leaf node (scalar or column)
leaf_type
:
	integer_type
	| oid_type
	| octet_string_type
	| bits_type
	| defined_type
;

integer_type
:
	integer_type_kw
	(named_number_list | range_constraint)?
;

integer_type_kw
:
	INTEGER_KW
	| "Integer32"
	| "Counter"
	| "Counter32"
	| "Gauge"
	| "Gauge32"
	| "Counter64"
	| "TimeTicks"
;

oid_type
:
	OBJECT_KW IDENTIFIER_KW
;

octet_string_type
:
	OCTET_KW STRING_KW (size_constraint)?
;

bits_type
:
	"BITS" (named_number_list)?
;

defined_type
:
	(UPPER DOT)? UPPER 
	(named_number_list | constraint)?
;

sequence_type
:
	SEQUENCE_KW
	L_BRACE
		LOWER leaf_type
		(COMMA LOWER leaf_type)*
	R_BRACE
;

sequenceof_type
:
	SEQUENCE_KW OF_KW UPPER
;


/* SMI v2: Sub-typing - defined in RFC 1902 section 7.1 and appendix C and RFC 1904 */
constraint
:
	range_constraint
	| size_constraint
;

// for integers
range_constraint
:
	L_PAREN range (BAR range)* R_PAREN
;

// for strings
size_constraint
:
	L_PAREN "SIZE" range_constraint R_PAREN
;

range
:
	range_value (DOTDOT range_value)?
;

range_value
:
	(MINUS)? NUMBER
	| B_STRING
	| H_STRING
;


// VALUES

value_assignment
:
	macro_value_assignment
	| primitive_value_assignment
;

primitive_value_assignment
:
	OBJECT_KW IDENTIFIER_KW ASSIGN_OP oid_value
;

macro_value_assignment
:
	oid_macro_value_assignment
	| int_macro_value_assignment
;

oid_macro_value_assignment
:
	(objecttype_macro
	| moduleidentity_macro 
	| objectidentity_macro
	| notificationtype_macro
	| objectgroup_macro
	| notificationgroup_macro
	| modulecompliance_macro
	| agentcapabilities_macro)
	ASSIGN_OP oid_value
	// TODO it's probably better to move the oid stuff into the macro def
;

int_macro_value_assignment
:
	traptype_macro ASSIGN_OP NUMBER
;


leaf_value
:
	integer_value
	| (bits_value) => bits_value
	| oid_value
	| octet_string_value
	| defined_value
;


oid_value
:
	L_BRACE
		(oid_component)+
	R_BRACE
;

oid_component
:
	NUMBER
	| LOWER (L_PAREN NUMBER R_PAREN)?
;

octet_string_value
:
	B_STRING|H_STRING|C_STRING
;

integer_value
:
	signed_number
;


bits_value
:
	L_BRACE (LOWER (COMMA LOWER)*)? R_BRACE
;

defined_value
:
	(UPPER DOT)? LOWER
;


objecttype_macro
:
	"OBJECT-TYPE" "SYNTAX"
		( leaf_type
		  | sequence_type
		  | sequenceof_type ) 
	("UNITS" C_STRING)? 
	( ("ACCESS" objecttype_access_v1)
		| ("MAX-ACCESS"  objecttype_access_v2) )? 
	"STATUS" status_all
	( "DESCRIPTION" C_STRING )? /* Optional only for SMIv1 */
	( "REFERENCE" C_STRING )? 	  
	( "INDEX" objecttype_macro_index
          | "AUGMENTS" objecttype_macro_augments )?
	( "DEFVAL" L_BRACE leaf_value R_BRACE )?
;

objecttype_access_v1
:
	l:LOWER
{
	ObjectTypeAccessV1.find(l.getText());
}
;

objecttype_access_v2
:
	l:LOWER
{
	ObjectTypeAccessV2.find(l.getText());
}
;

status_all
:
	l:LOWER
{
	StatusAll.find(l.getText());
}
;

status_v1
:
	l:LOWER
{
	StatusV1.find(l.getText());
}
;

status_v2
:
	l:LOWER
{
	StatusV2.find(l.getText());
}
;


objecttype_macro_index
:
	L_BRACE
		objecttype_macro_indextype
		(COMMA objecttype_macro_indextype)*
	R_BRACE
;    
   
objecttype_macro_indextype
:
	("IMPLIED")? defined_value
;

objecttype_macro_augments
:
	L_BRACE defined_value R_BRACE
;


moduleidentity_macro
:
	"MODULE-IDENTITY" 
	"LAST-UPDATED" C_STRING
	"ORGANIZATION" C_STRING
	"CONTACT-INFO" C_STRING 
	"DESCRIPTION"  C_STRING
	(moduleidentity_macro_revision)*
;

moduleidentity_macro_revision
:
	"REVISION"    C_STRING
	"DESCRIPTION" C_STRING
;


objectidentity_macro
:
	"OBJECT-IDENTITY"
	"STATUS" status_v2
	"DESCRIPTION" C_STRING ("REFERENCE" C_STRING)?
;


notificationtype_macro
:
	"NOTIFICATION-TYPE"
	("OBJECTS" L_BRACE LOWER (COMMA LOWER)* R_BRACE)? 
	"STATUS" status_v2
	"DESCRIPTION" C_STRING ("REFERENCE" C_STRING)?
;

textualconvention_macro
:
	"TEXTUAL-CONVENTION"
	("DISPLAY-HINT" C_STRING)?
	"STATUS" status_v2 
	"DESCRIPTION" C_STRING 
	("REFERENCE" C_STRING)? 
	"SYNTAX" leaf_type
;


objectgroup_macro
:
	"OBJECT-GROUP" "OBJECTS"
	L_BRACE
		LOWER (COMMA LOWER)*
	R_BRACE 
	"STATUS" status_v2
	"DESCRIPTION" C_STRING
	("REFERENCE" C_STRING)?
;


notificationgroup_macro
:
	"NOTIFICATION-GROUP" "NOTIFICATIONS"
	L_BRACE
		LOWER (COMMA LOWER)*
	R_BRACE 
	"STATUS" status_v2
	"DESCRIPTION" C_STRING
	("REFERENCE" C_STRING)?
;


modulecompliance_macro
:
	"MODULE-COMPLIANCE"
	"STATUS" status_v2
	"DESCRIPTION" C_STRING
	("REFERENCE" C_STRING)?
	(modulecompliance_macro_module)+
;

modulecompliance_macro_module
:
	"MODULE" (UPPER)? 
	("MANDATORY-GROUPS" L_BRACE LOWER (COMMA LOWER)* R_BRACE)?
	(modulecompliance_macro_compliance)*
;

modulecompliance_macro_compliance
:
	modulecompliance_macro_compliance_group
	| modulecompliance_macro_compliance_object
;

modulecompliance_macro_compliance_group
:
	"GROUP" LOWER
	"DESCRIPTION" C_STRING
;

modulecompliance_macro_compliance_object
:
	"OBJECT" LOWER
	("SYNTAX" leaf_type)? 
	("WRITE-SYNTAX" leaf_type)?
	("MIN-ACCESS" modulecompliance_access)? 
	"DESCRIPTION" C_STRING
;

modulecompliance_access
:
	l:LOWER
{
	ModuleComplianceAccess.find(l.getText());
}
;


agentcapabilities_macro
:
	"AGENT-CAPABILITIES"
	"PRODUCT-RELEASE" C_STRING
	"STATUS" agentcapabilities_status
	"DESCRIPTION" C_STRING
	("REFERENCE" C_STRING)?
	(agentcapabilities_macro_module)*
;

agentcapabilities_status
:
	l:LOWER
{
	AgentCapabilitiesStatus.find(l.getText());
}
;

agentcapabilities_macro_module
:
	"SUPPORTS" UPPER
	"INCLUDES" L_BRACE LOWER (COMMA LOWER)* R_BRACE 
	(agentcapabilities_macro_variation)*
;

agentcapabilities_macro_variation
:
	"VARIATION" LOWER
	("SYNTAX" leaf_type)?
	("WRITE-SYNTAX" leaf_type)?
	("ACCESS" agentcapabilities_access)? 
	("CREATION-REQUIRES" L_BRACE LOWER (COMMA LOWER)* R_BRACE)? 
	("DEFVAL" L_BRACE leaf_value R_BRACE)?
	"DESCRIPTION" C_STRING
;


agentcapabilities_access
:
	l:LOWER
{
	AgentCapabilitiesAccess.find(l.getText());
}
;


traptype_macro
:
	"TRAP-TYPE"
	"ENTERPRISE" LOWER
	("VARIABLES" L_BRACE LOWER (COMMA LOWER)* R_BRACE)? 
	("DESCRIPTION" C_STRING)?
	("REFERENCE" C_STRING)?
;

named_number_list
:
	L_BRACE named_number (COMMA named_number)* R_BRACE
;

named_number
:
	LOWER L_PAREN signed_number R_PAREN
;

signed_number
:
	(MINUS)? NUMBER
;
