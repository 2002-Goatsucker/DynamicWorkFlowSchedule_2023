import numpy as np
import matplotlib.pyplot as plt

data = np.loadtxt('D:\\result.txt')
plt.plot(data[:,0], data[:,1], 'ro')
print(data)

plt.title('Scatter Plot')
plt.xlabel('x')
plt.ylabel('y')
plt.savefig('images\\scatter.png')