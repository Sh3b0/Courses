% 10.1
% student (Name, Group)
student(alisa, 2).
student(bob,  1).
student(chloe, 2).
student(denise, 1).
student(edward, 2).

groupmates(X, Y) :-
	student(X, G), student(Y, G), X \= Y.

% groupmates(alisa, bob)
% groupmates(alisa, edward)

% 10.2
friend(alisa, bob).
friend(alisa, denise).
friend(bob, chloe).
friend(bob, edward).
friend(chloe, denise).
friend(denise, edward).

% friend(alisa, Y), friend(Y, Z).

% from the trace output.
% Call:friend(alisa, Y)
  % Exit:friend(alisa, bob)
    % Call:friend(bob, Z)
    	% Exit:friend(bob, chloe)
  	% Redo:friend(bob, Z)
  		% Exit:friend(bob, edward)
% Redo:friend(alisa, Y)
	% Exit:friend(alisa, denise)
		% Call:friend(denise, Z)
			% Exit:friend(denise, edward)
	
% 10.3
unary(z).
unary(s(X)) :- unary(X).

add(z, Y, Y).
add(s(X), Y, s(R)) :- add(X, Y, R).

mult(z, _, z).
mult(s(X), Y, Z) :- mult(X, Y, R), add(Y, R, Z).
% mult(s(s(z)), s(s(s(z))), X).
% mult(X, s(s(s(z))), s(s(s(s(s(s(z))))))).

% 11.1
sublist([], _). % empty list is a sublist of any list
sublist([H|T1], [H|T2]) :- sublist(T1, T2).

% It's possible to ignore a head while checking sublists.
% usage of [H|T1] avoids extra calls with empty lists.
sublist([H|T1], [_|T2]) :- sublist([H|T1], T2).

% sublist([2, 3, 5] , [1, 2, 3, 4, 5]).
% sublist(X, [1, 2]).

% 11.2
search([], _). % empty list occurs in any list.
search([H|T1], [H|T2]) :- search(T1, T2). % When heads are equal, check tails.
search(L1, L2, 0) :- search(L1, L2). % start here

% Consume second list, keeping track of start position, until a prefix is found.
search(L1, [_|T2], N) :- search(L1, T2, K), N is K+1. 

% search(a,b,a), [c,a,b,a,b,a,d], Pos).
% search(Needle, [c,a,b,a,b,a,d], 5).

% 11.3
% replace (Match, Replace, S, T) checks whether T
% can be constructed from S by replacing
% some occurrences of Match with Replace

replace(_, _, [], []).
replace(Match, Replace, S, T) :-
    append(Match, Rest, S),
    replace(Match, Replace, Rest, NewT),
    append(Replace, NewT, T).

replace(Match, Replace, [H|S], [H|T]) :-
    replace(Match, Replace, S, T).

% replace([a,b], [x,y,z], [a,b,r,a,b,a], L).
% replace([a,a], [x,y], [a,a,a,a], L).

% 12.1
minimum([H|T], Min) :- minimum(T, H, Min). % start with head as minimum so far.
minimum([], Min, Min). % list was consumed, terminate with Min = MinSoFar
minimum([H|T], MinSoFar, Min) :-
    Tmp is min(H, MinSoFar),
    minimum(T, Tmp, Min).

% minimum([3, 6, 2, 5, 4, 7], X). 

% 12.2
remove(_, [], []). % Nothing can be removed from an empty list 
remove(E, [E|T], Result) :- % If H = E exclude it from the result.
    remove(E, T, Result).

remove(E, [H|T1], [H|T2]) :- % Otherwise, prepend H to the result.
    H \= E, remove(E, T1, T2).

% remove(e, [a,p,p,l,e,p,i,e], X).

% 12.3
% The difference between this one and the previous one is that
% here we don't math H with E exactly, but rather try to unify them
% double negation will test unification without printing the tried values.
removeU(_, [], []).
removeU(E, [H|T], Result) :- 
    \+ (H \= E), !, removeU(E, T, Result).

removeU(E, [H|T1], [H|T2]) :-
    H \= E, removeU(E, T1, T2).
% removeU(t(X), [1, a, A, tb, t(b), tX, t(B)], Y).

% 12.4
% Predicate for natural numbers
nat(0).
nat(N) :- nat(K), N is K+1.

% Bounding natural numbers by Max, so that prime checker terminates.
nat(0, 0) :- !.
nat(0, Max) :- Max > 0.
nat(N, Max) :- M is Max-1, nat(K, M), N is K+1.

% N is prime if it's a non-composite natural number greater than 1.
prime(N) :- nat(N), N > 1, \+ composite(N).

% N is composite if there exists X * Y = N where X, Y \in (1, N) 
composite(N) :-
	nat(X, N), 1 < X, X < N,
    nat(Y, N), 1 < Y, Y < N,
	N is X * Y.

% prime(29).
% prime(N).

% 12.5
color(red).
color(green).
color(blue).

distinctColors([]).
distinctColors([H|T]) :-
    color(H),
    distinctColors(T),
    \+ member(H, T).

% a list of all colors is a list of distinct colors such that
% there does not exist a color that is not in that list.
allColors(L) :-
    distinctColors(L),
    \+ (color(C), \+ member(C, L)).

% allColors([red, green, blue]).
% allColors([red, G, blue]).
% allColors(Colors).
