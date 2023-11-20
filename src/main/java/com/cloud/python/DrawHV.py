import numpy as np
import matplotlib.pyplot as plt

if __name__ == "__main__":
    plt.figure(0);
    x = [i + 1 for i in range(500)]
    data1 = np.loadtxt("C:/Users/徐璟源/Desktop/git repository/DynamicScheduling_2023/src/main/resources/result/result_nsgaiib.txt")
    data2 = np.loadtxt("C:/Users/徐璟源/Desktop/git repository/DynamicScheduling_2023/src/main/resources/result/result_fogmp.txt")
    data3 = np.loadtxt("C:/Users/徐璟源/Desktop/git repository/DynamicScheduling_2023/src/main/resources/result/result_idg.txt")
    data4 = np.loadtxt("C:/Users/徐璟源/Desktop/git repository/DynamicScheduling_2023/src/main/resources/result/result_mb.txt")

    plt.plot(x, data1)
    plt.plot(x, data2)
    plt.plot(x, data3)
    plt.plot(x, data4)
    plt.legend(['DNSGAIIB', 'FOGMP', 'DNSGAIIgIDG', 'MBNSGAII'])
    plt.title("Montage_100 80ins 0.03severity 100task")
    plt.show()