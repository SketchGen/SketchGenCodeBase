module handshake
open util/integer [] as integer
sig Person {
spouse: (one Person),
shaken: (set Person)
}
one sig Jocelyn extends Person {}
one sig Hilary extends Person {}