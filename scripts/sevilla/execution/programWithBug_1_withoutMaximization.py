import coverage
import __main__ as main
import numpy as np
import sys
import inspect
import math

def max(x, y, z):
	if (x < y):							#l1
		max = x #Fault -> max = y		#l2
	else: max = x						#l3
	if (max < z):						#l4
		max = z							#l5
	return max							#l6

	
def test_case_1():
	x = 1
	y = 5
	z = 3
	expectedOutput = 5
	assert max(x, y, z) == expectedOutput, "Should be 5"

def test_case_2():
	x = 3
	y = 2
	z = 1
	expectedOutput = 3
	assert max(x, y, z) == expectedOutput, "Should be 3"

def test_case_3():
	x = 3
	y = 2
	z = 6
	expectedOutput = 6
	assert max(x, y, z) == expectedOutput, "Should be 6"
	
#Get a list of all test cases functions (those that starts with test_cases_)
def getTestCases():
	testCases = []
	for name, obj in inspect.getmembers(sys.modules[__name__]):
		if (inspect.isfunction(obj) and name.startswith('test_case_')):
			testCases.append(name)
	return testCases
	
#get the suspiciousness of an entity according to a ranking metric
def getSuspiciousness(rankingMetric, ncf, nuf, ncs, nus, nc, nu, ns, nf):
	if rankingMetric == "OCHIAI_1":
		print("nf: " + str(nf) + " ncf:" + str(ncf) + " ncs:" + str(ncs) + " suma:" + str(ncf + ncs) + " mult:" +  str(nf * (ncf + ncs)) + " raiz: " + str(math.sqrt(nf * (ncf + ncs))))
		return (ncf)/math.sqrt(nf * (ncf + ncs));
	elif rankingMetric == "TARANTULA":
		return (ncf/nf)/(ncf/nf + ncs/ns);
		
if __name__ == "__main__":
	#Prepare the coverage
	cov = coverage.Coverage(auto_data=False)
	cov.exclude("if __name__ == \"__main__\":")
	cov.exclude("def test_case_*")
	cov.exclude("def getTestCases()")
	cov.exclude("def getSuspiciousness*")
	cov.exclude("import *")
	linesOfCode = cov.analysis2(main.__file__)[1][1:]
	
	coverageMatrix = np.empty([len(linesOfCode), 0]) #each column is a test case, and each row is a line under cover. The vaulues indicates 1 if the test case cover the line, and 0 if not
	failureVector = []
	
	#obtain the coverage for all test suite
	testCases = getTestCases()	
	for testCase in testCases:
		#start coverage of the test case
		cov.start()
		
		#execute the test case
		try:
			print(testCase + ": ", end = "")
			getattr(sys.modules[__name__], testCase)()
			print("Good")
			failureVector.append(0)
		except AssertionError:
			print("Failure")
			failureVector.append(1)
		#end the coverage of the test case
		cov.stop()
		
		coverageData = cov.get_data()
		fileName = coverageData.measured_files()[0]
		linesCovered = coverageData.lines(fileName)
		print("\t lines covered: " + str(linesCovered))
		
		coverageVector = []
		for lineOfCode in linesOfCode:
			if lineOfCode in linesCovered:
				coverageVector.append(1)
			else:
				coverageVector.append(0)
		print("\t coverage vector: " + str(coverageVector))
		
		#remove all information of the coverage
		cov.erase()
		coverageData.erase()
		
		#add the coverage vector of the test case to the coverageMatrix
		coverageVector = np.asarray(coverageVector).reshape((len(coverageVector),1))
		coverageMatrix = np.column_stack((coverageMatrix, coverageVector))
	
	#some checks
	assert np.size(coverageMatrix, 0) == len(linesOfCode)
	assert np.size(coverageMatrix, 1) == len(testCases )

	
	
	factors = ["Mem: 90MB", "Mem: 240MB",
				"CPU 1 core", "CPU 4 core",
				 "S 800x600", "S 1366x768", "S 1920x1024"]
	
	
	coverageMatrix = np.array([
						[1,0,    1,0,    1,0,0],
						[1,0,    1,0,    0,1,0],
						[1,0,    1,0,    0,0,1],
						[0,1,    0,1,    1,0,0],
						[0,1,    1,0,    0,0,1],
						[0,1,    1,0,    0,1,0],		
						])
	coverageMatrix = np.transpose(coverageMatrix)
	failureVector = [1, 1, 0, 1, 0, 1]
	
	
	print("\nCoverage Matrix:")
	print(coverageMatrix)
	
	print("\nFailure vector:" + str(failureVector))
	
	
	#get suspiciousness
	linesSuspiciousness = []
	'''
		ncf: Number of failures that cover the line after the test cases
		nuf: Number of failures that do not cover the line after the test cases
		ncs: Number of success test cases that cover the line
		nus: Number of success test cases that do not cover the line
		nc: Number of test cases that cover the line (regardless if the execution of the test cases that fail or not)
		nu: Number of test cases that do not cover the line (regardless if the test cases fail or not)
		ns: Number of success test cases (regardless if the test case cover the line or not)
		nf: Number of failures (regardless if the test cases cover the line or not)
	'''
	for coverageLine in coverageMatrix:
		ncf = float(0)
		nuf = float(0)
		ncs = float(0)
		nus = float(0)
		nc = float(0)
		nu = float(0)
		ns = float(0)
		nf = float(0)
		for testCaseIndex in range(0, np.size(coverageMatrix,1), 1):
			causeFailure = failureVector[testCaseIndex] == 1
			coverLine = coverageLine[testCaseIndex] == 1
			
			if causeFailure and coverLine:
				ncf = ncf + 1
			if causeFailure and not coverLine:
				nuf = nuf + 1
			if not causeFailure and coverLine:
				ncs = ncs + 1
			if not causeFailure and not coverLine:
				nus = nus + 1
			if coverLine:
				nc = nc + 1
			if not coverLine:
				nu = nu + 1
			if not causeFailure:
				ns = ns + 1
			if causeFailure:
				nf = nf + 1
		
		print("debug: " + str(coverageLine))
		lineSuspiciousness = getSuspiciousness("OCHIAI_1", ncf, nuf, ncs, nus, nc, nu, ns, nf)
		linesSuspiciousness.append(lineSuspiciousness)
		
	print("\nSuspiciousness: " + str(linesSuspiciousness))
	
	#calculate the ranking
	ranking = []
	for suspiciousness in linesSuspiciousness:
		rankSuspiciousness =  sorted(linesSuspiciousness, reverse=True).index(suspiciousness) + 1
		ranking.append(rankSuspiciousness)
	
	print("\nRanking: " + str(ranking))
	
	
	## clear print
	print("\nHuman readable print:")
	print("Coverage Matrix & failure vector:")
	header = "\t"
	for indexTestCase in range(0, np.size(coverageMatrix,1), 1):
		header = header + "\tTC" + str(indexTestCase + 1)
	header = header + "\t| Suspiciousness\tRanking"
	print(header, end = "")
	
	for rowIndex in range(0, np.size(coverageMatrix,0), 1):
		print("\nl" + str(rowIndex) + " " + str(factors[rowIndex]), end = "")
		for columnIndex in range(0, np.size(coverageMatrix,1), 1):
			print("\t" + str(coverageMatrix[rowIndex][columnIndex]) , end = "")
		print("\t| " + str(round(linesSuspiciousness[rowIndex], 3)) + "\t\t\t|" + str(ranking[rowIndex]))
		
	print("\n----------\nFailure -> ", end = "")
	for testCaseIndex in range(0, np.size(coverageMatrix,1), 1):
		print("\t" + str(failureVector[testCaseIndex]), end = "")
	print()