module examples/toys/genealogy
open util/integer [] as integer
abstract sig Person {
spouse: (lone Person),
parents: (set Person)
}
sig Man extends Person {}
sig Woman extends Person {}
one sig Eve extends Woman {}
one sig Adam extends Man {}