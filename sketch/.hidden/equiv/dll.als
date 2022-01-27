module unknown
open util/integer [] as integer
one sig DLL {
header: (lone Node)
}
sig Node {
pre,nxt: (lone Node),
elem: (one Int)
}