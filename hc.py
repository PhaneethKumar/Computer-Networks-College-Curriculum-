data = input("Enter the binary data: ")
m = len(data)

r = 0
while 2 ** r < m + r + 1:          # number of parity bits
    r += 1

n = m + r
h = [0] * (n + 1)                  # 1-indexed

bits = iter(data[::-1])            # fill data bits from the right
for i in range(1, n + 1):
    if i & (i - 1):                # not a power of 2 -> data position
        h[i] = int(next(bits))

for i in range(r):                 # even parity for each parity bit
    p = 2 ** i
    h[p] = sum(h[k] for k in range(1, n + 1) if k & p) % 2

print("Generated Hamming Code:", ''.join(map(str, h[:0:-1])))