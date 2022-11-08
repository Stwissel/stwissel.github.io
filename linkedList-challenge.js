/* Linked Node class function */
function LinkedNode(value) {
  this.value = value;
  this.next = null;
}

let one = new LinkedNode('A');
let two = new LinkedNode('B');
let three = new LinkedNode('C');
let four = new LinkedNode('D');

one.next = two;
two.next = three;
three.next = four;

function reverseList(head) {
  /* Your code here */
}

console.log(JSON.stringify(one, null, 2));
let reversed = reverseList(one);
console.log(JSON.stringify(reversed, null, 2));
/* D C B A */
