module deadlock
open util/integer [] as integer
sig Process {}
sig Mutex {}
sig State {
holds,waits: (Process->Mutex)
}