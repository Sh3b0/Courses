unordered_map<int,int>mem;
int fib(int idx){ // 1-indexed fibbonacci: 1, 1, 2, ...
	if(idx == 1 || idx == 2) return mem[idx] = 1;
	if(mem[idx]) return mem[idx];
	return mem[idx] = fib(idx-1) + fib(idx-2);
}

// OR

int fib(int idx){ // 1-indexed fibbonacci: 1, 1, 2, ...
	return ( pow((1+sqrt(5))/2.0, idx) - pow((1-sqrt(5))/2.0, idx) ) / sqrt(5);
}