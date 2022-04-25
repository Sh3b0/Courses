long long fact(int x){
	if(x < 3) return x;
	return x * fact(x-1);
}