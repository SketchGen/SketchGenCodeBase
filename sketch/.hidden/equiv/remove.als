module unknown
open util/integer [] as integer
one sig List {
header,h_post: (lone Node)
}
sig Node {
elem,e_post: (lone Object),
link,l_post: (lone Node)
}
sig Object {}