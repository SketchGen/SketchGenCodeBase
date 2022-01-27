module unknown
open util/integer [] as integer
sig Room {}
sig Employee {
owns: (set Key)
}
sig Key {
authorized: (one Employee),
opened_by: (one Room)
}