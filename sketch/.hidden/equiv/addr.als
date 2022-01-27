module unknown
open util/integer [] as integer
abstract sig Listing {}
sig Address extends Listing {}
sig Name extends Listing {}
sig Book {
entry: (set Name),
listed: (entry->Listing)
}