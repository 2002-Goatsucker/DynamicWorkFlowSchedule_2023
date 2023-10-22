from matplotlib import pyplot as plt
import sys
import numpy as np
from scipy.ndimage import gaussian_filter1d

len = len(sys.argv)
y = np.empty(len-1, dtype=float)
for i in range(1, len):
    y[i-1] = sys.argv[i];


x=np.arange(y.size)

plt.plot(x, y)
plt.title("Spline Curve Using the Gaussian Smoothing")
plt.xlabel("X")
plt.ylabel("Y")
plt.show()