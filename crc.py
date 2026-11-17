def xor(a, b):
    return ''.join('0' if x == y else '1' for x, y in zip(a[1:], b[1:]))

def mod2div(dividend, divisor):
    n = len(divisor)
    tmp = dividend[:n]
    for i in range(n, len(dividend)):
        tmp = xor(divisor if tmp[0] == '1' else '0' * n, tmp) + dividend[i]
    return xor(divisor if tmp[0] == '1' else '0' * n, tmp)

data = input("Enter Data: ")
gen = input("Enter Generator: ")

rem = mod2div(data + '0' * (len(gen) - 1), gen)
print("CRC Remainder:", rem)
print("Transmitted Codeword:", data + rem)