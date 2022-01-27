module unknown
open util/integer [] as integer
one sig List {
header: (lone Node)
}
sig Node {
elem: (lone Object),
link: (lone Node)
}
sig Object {}