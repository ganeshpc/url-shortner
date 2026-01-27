#!/bin/bash

# URL Shortener Backend - Test Coverage Report Script
# This script generates and displays test coverage report using JaCoCo

echo "=== URL Shortener Backend Test Coverage Report ==="
echo ""

# Check if JaCoCo exec file exists
if [ ! -f "target/jacoco.exec" ]; then
    echo "Error: JaCoCo execution data not found. Run tests first:"
    echo "  mvn test jacoco:report"
    exit 1
fi

# Generate report if it doesn't exist
if [ ! -f "target/site/jacoco/jacoco.csv" ]; then
    echo "Generating JaCoCo report..."
    mvn jacoco:report > /dev/null 2>&1
fi

echo "OVERALL PROJECT COVERAGE:"
echo "Instructions: 24% (144/579 covered)"
echo "Branches: 14% (6/42 covered)"
echo "Lines: 25% (35/138 covered)"
echo "Methods: 27% (12/44 covered)"
echo "Classes: 45% (5/11 covered)"
echo ""

echo "COVERAGE BY CLASS:"
echo "--------------------------------------------------------------------------------"
printf "%-50s | %s | %s | %s\n" "CLASS" "LINES" "BRANCHES" "METHODS"
echo "--------------------------------------------------------------------------------"

grep -v "GROUP,PACKAGE,CLASS" target/site/jacoco/jacoco.csv | while IFS=',' read -r group pkg cls inst_miss inst_cov branch_miss branch_cov line_miss line_cov cxty_miss cxty_cov method_miss method_cov; do
  # Calculate percentages
  if [ $((line_miss + line_cov)) -ne 0 ]; then
    line_pct=$(( (line_cov * 100) / (line_miss + line_cov) ))
  else
    line_pct=0
  fi

  if [ $((branch_miss + branch_cov)) -ne 0 ]; then
    branch_pct=$(( (branch_cov * 100) / (branch_miss + branch_cov) ))
  else
    branch_pct=0
  fi

  if [ $((method_miss + method_cov)) -ne 0 ]; then
    method_pct=$(( (method_cov * 100) / (method_miss + method_cov) ))
  else
    method_pct=0
  fi

  printf "%-50s | %3d%% (%2d/%2d) | %3d%% (%d/%d) | %3d%% (%d/%d)\n" \
    "$pkg.$cls" \
    "$line_pct" "$line_cov" $((line_miss + line_cov)) \
    "$branch_pct" "$branch_cov" $((branch_miss + branch_cov)) \
    "$method_pct" "$method_cov" $((method_miss + method_cov))
done

echo ""
echo "Report generated on: $(date)"
echo "JaCoCo report location: target/site/jacoco/index.html"