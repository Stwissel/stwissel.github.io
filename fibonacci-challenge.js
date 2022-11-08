/*
 This function computes the fibonacci numbers for a given input
 based on the fibonacci model whee each number is the sum of the
 preceding 2 numbers, but at least 1.

 0 -> 1
 1 -> 1
 2 -> 2
 3 -> 3
 4 -> 5
 5 -> 8
 6 -> 13
 7 -> 21
 8 -> 34

 etc ...
*/

function fib(n) {
  if (n === 0 || n === 1) {
    return 1;
  }

  return fib(n - 1) + fib(n - 2);
}

// This is fine, no change required
let args = process.argv;

if (args.length < 3) {
  console.error('Usage: node fibonacci-challenge [number]');
  return 1;
}

// try 42

let number = Number(args[2]);
let startTime = Date.now();
let result = fib(number);
let endTime = Date.now();
let duration = endTime - startTime;
console.log(`Fibonacci for ${number} is ${result} (took ${duration} ms)`);
